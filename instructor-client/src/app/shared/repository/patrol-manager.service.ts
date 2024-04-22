import { Injectable } from '@angular/core';
import {environment} from "../../../../env/environment";
import ExamineeDataService from "./examinee-data.service";

@Injectable({
  providedIn: 'root'
})
export class PatrolManagerService {
  constructor(private examineeRepo: ExamineeDataService) {
    this.nextClientTime = environment.nextClientScheduleTime; // start the interval
    this.nextPatrol = environment.patrolSpeed; // start the interval
  }

  private nextClientScheduleTime: number = 0;
  private patrolSpeed: number = 0;
  private clientScheduleTimer: number | undefined;
  private patrolTimer: number | undefined;

  stopClientScheduleInterval() {
    if (this.clientScheduleTimer !== undefined) {
      clearInterval(this.clientScheduleTimer);
    }

    this.clientScheduleTimer = undefined;
  }


  stopPatrolInterval() {
    if (this.patrolTimer !== undefined) {
      clearInterval(this.patrolTimer);
    }

    this.patrolTimer = undefined;
  }

  startClientScheduleInterval() {
    this.stopClientScheduleInterval();

    if (this.clientScheduleTimer === undefined) {
      this.clientScheduleTimer = setInterval(() => {
        this.examineeRepo.updateScreenshots();
      }, this.nextClientScheduleTime);
    }
  }

  startPatrolInterval() {
    this.stopPatrolInterval();

    if (this.patrolTimer === undefined) {
      this.patrolTimer = setInterval(() => {
        this.examineeRepo.newPatrolExaminee();
      }, this.patrolSpeed);
    }
  }

  getNextClientTimeFormatted() {
    return this.nextClientScheduleTime/1000;
  }

  getPatrolSpeedFormatted() {
    return this.patrolSpeed/1000;
  }

  get nextClientTime() {
    return this.nextClientScheduleTime/1000;
  }

  set nextClientTime(val: number) {
    if (val >= environment.minNextClientScheduleTime && val <= environment.maxNextClientScheduleTime) {
      this.nextClientScheduleTime = val*1000;
      this.startClientScheduleInterval();
    }
  }

  get nextPatrol() {
    return this.patrolSpeed/1000;
  }

  set nextPatrol(val: number) {
    if (val >= environment.minPatrolSpeed && val <= environment.maxPatrolSpeed) {
      this.patrolSpeed = val*1000;
      this.startPatrolInterval();
    }
  }
}
