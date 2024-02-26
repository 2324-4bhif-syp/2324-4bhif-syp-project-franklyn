import { Injectable } from '@angular/core';
import {environment} from "../../../../env/environment";
import ExamineeDataService from "./examinee-data.service";

@Injectable({
  providedIn: 'root'
})
export class PatrolManagerService {
  constructor(private examineeRepo: ExamineeDataService) {
    this.nextClientTime = environment.nextClientScheduleTime; // start the interval
  }

  private nextClientScheduleTime: number = 0;
  private timer: number | undefined;

  stopInterval() {
    if (this.timer !== undefined) {
      clearInterval(this.timer);
    }

    this.timer = undefined;
  }

  startInterval() {
    this.stopInterval();

    if (this.timer === undefined) {
      this.timer = setInterval(() => {
        this.examineeRepo.newPatrolExaminee();
      }, this.nextClientScheduleTime);
    }
  }

  get nextClientTime() {
    return this.nextClientScheduleTime/1000;
  }

  set nextClientTime(val: number) {
    if (val >= environment.minNextClientScheduleTime && val <= environment.maxNextClientScheduleTime) {
      this.nextClientScheduleTime = val*1000;
      console.log(this.nextClientScheduleTime);
      this.startInterval();
    }
  }
}
