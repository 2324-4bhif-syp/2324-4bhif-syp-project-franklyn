import {Examinee} from "./Examinee";

export interface Patrol {
  nextClientScheduleTime: number,
  patrolSpeed: number,
  clientScheduleTimer: number | undefined,
  patrolTimer: number | undefined,
  patrolModeOn: boolean,
  patrolExaminee: Examinee | undefined
}
