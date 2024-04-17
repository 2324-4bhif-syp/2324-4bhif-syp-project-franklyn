import {Examinee} from "./Examinee";
import {environment} from "../../../../env/environment";

export class Patrol {
  constructor(
    nextClientScheduleTime: number,
    patrolSpeed: number,
    patrolModeOn: boolean) {
    this.nextClientTime = nextClientScheduleTime;
    this.nextPatrol = patrolSpeed;
    this.isPatrolModeOn = patrolModeOn;
  }

  private nextClientScheduleTime: number = 0;
  private patrolSpeed: number = 0;
  private clientScheduleTimer: number | undefined;
  private patrolScheduleTimer: number | undefined;
  private isPatrolModeOn: boolean;
  private curPatrolExaminee: Examinee | undefined;

  get nextClientTimeUnformatted() {
    return this.nextClientScheduleTime;
  }

  get nextClientTime() {
    return this.nextClientScheduleTime/1000;
  }

  set nextClientTime(val: number) {
    if (val >= environment.minNextClientScheduleTime && val <= environment.maxNextClientScheduleTime) {
      this.nextClientScheduleTime = val*1000;
    }
  }

  get nextPatrolUnformatted() {
    return this.patrolSpeed;
  }

  get nextPatrol() {
    return this.patrolSpeed/1000;
  }

  set nextPatrol(val: number) {
    if (val >= environment.minPatrolSpeed && val <= environment.maxPatrolSpeed) {
      this.patrolSpeed = val*1000;
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
    return this.curPatrolExaminee;
  }

  set patrolExaminee(val: Examinee | undefined) {
    this.curPatrolExaminee = val;
  }
}
