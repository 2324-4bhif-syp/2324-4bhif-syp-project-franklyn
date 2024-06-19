import {ExamState} from "./Exam-State";

export interface Exam {
  id: number,
  plannedStart: Date,
  plannedEnd: Date,
  actualEnd: Date,
  title: string,
  pin: number,
  state: ExamState
}
