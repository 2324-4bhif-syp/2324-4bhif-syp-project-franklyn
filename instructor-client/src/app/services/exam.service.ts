import {inject, Injectable} from '@angular/core';
import {WebApiService} from "./web-api.service";
import {set} from "../model";
import {Exam} from "../model/entity/Exam";
import {StoreService} from "./store.service";
import {Location} from "@angular/common";
import {ExamDto} from "../model/entity/dto/exam-dto";

@Injectable({
  providedIn: 'root'
})
export class ExamService {
  private store = inject(StoreService).store;
  private location = inject(Location);
  private webApi = inject(WebApiService);

  constructor() {
    this.reloadAllExams();
  }

  reloadAllExams(): void {
    this.webApi.getExamsFromServer();
  }

  reloadSpecificExam(exam: Exam): void {
    this.webApi.getExamByIdFromServer(exam.id);
  }

  get(predicate?: ((item: Exam) => boolean) | undefined): Exam[] {
    if (predicate) return this.get().filter(predicate);
    return this.store.value.examData.exams;
  }

  setCurExam(exam: Exam) {
    let exams = this.get((e) =>
      e.id === exam.id
    );

    let newCurExam: Exam | undefined;

    if (exams.length === 1)
      newCurExam = exams[0];

    set((model) => {
      model.examData.curExam = newCurExam;
    });
  }

  deleteSpecificExam(exam: Exam): void {
    this.webApi.deleteExamByIdFromServer(exam.id);
  }

  createNewExam(exam: ExamDto): void {
    this.webApi.createNewExam(exam);
  }
}
