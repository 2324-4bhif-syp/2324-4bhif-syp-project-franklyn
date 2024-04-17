import {Examinee} from "./Examinee";
import {environment} from "../../../../env/environment";
import {inject} from "@angular/core";
import {ScheduleService} from "../../services/schedule.service";

export class Patrol {
  constructor(
    nextClientScheduleTime: number,
    patrolSpeed: number,
    patrolModeOn: boolean) {
    this.nextClientScheduleTime = nextClientScheduleTime;
    this.patrolSpeed = patrolSpeed;
    this.isPatrolModeOn = patrolModeOn;
  }

  private nextClientScheduleTime: number;
  private patrolSpeed: number;
  private clientScheduleTimer: number | undefined;
  private patrolScheduleTimer: number | undefined;
  private isPatrolModeOn: boolean;
  private curPatrolExaminee: Examinee | undefined;

  private scheduleSvc = inject(ScheduleService);

  get nextClientTime() {
    return this.nextClientScheduleTime/1000;
  }

  set nextClientTime(val: number) {
    if (val >= environment.minNextClientScheduleTime && val <= environment.maxNextClientScheduleTime) {
      this.nextClientScheduleTime = val*1000;
      this.scheduleSvc.startExamineeScheduleInterval();
    }
  }

  get nextPatrol() {
    return this.patrolSpeed/1000;
  }

  set nextPatrol(val: number) {
    if (val >= environment.minPatrolSpeed && val <= environment.maxPatrolSpeed) {
      this.patrolSpeed = val*1000;
      this.scheduleSvc.startPatrolInterval();
    }
  }

  get clientTimer(): number | undefined {
    return this.clientScheduleTimer;
  }

  set clientTimer(val: number | undefined) {
    this.clientScheduleTimer = val;
  }

  get patrolTimer(): number | undefined {
    return this.patrolScheduleTimer;
  }

  set patrolTimer(val: number | undefined) {
    this.patrolScheduleTimer = val;
  }

  get patrolModeOn(): boolean {
    return this.isPatrolModeOn;
  }

  set patrolModeOn(val: boolean) {
    this.isPatrolModeOn = val;
  }

  get patrolExaminee(): Examinee | undefined {
    return this.patrolExaminee;
  }

  set patrolExaminee(val: number) {
    this.patrolExaminee = val;
  }
}
