import {Exam} from "./Exam";

export interface ExamData {
  exams: Exam[],
  curExam: Exam | undefined
}
