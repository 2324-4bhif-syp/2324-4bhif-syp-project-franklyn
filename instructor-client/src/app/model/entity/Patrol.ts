import {Examinee} from "./Examinee";

export interface Patrol {
  isPatrolModeOn: boolean;
  patrolExaminee: Examinee | undefined;
}
