import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Examinee, ServerMetrics} from "../model";
import {environment} from "../../../env/environment";
import {lastValueFrom} from "rxjs";
import {set} from "../model";
import {Exam} from "../model/entity/Exam";
import {ExamDto} from "../model/entity/dto/exam-dto";
import {ExamState} from "../model/entity/Exam-State";

@Injectable({
  providedIn: 'root'
})
export class WebApiService {
  private httpClient = inject(HttpClient);
  private headers: HttpHeaders = new HttpHeaders().set('Accept', 'application/json');

  public async getExamineesFromServer(examId: number): Promise<void> {
      this.httpClient.get<Examinee[]>(
        `${environment.serverBaseUrl}/exams/${examId}/examinees`,
        {headers: this.headers})
        .subscribe({
        "next": (examinees) => set((model) => {
          model.examineeData.examinees = examinees;
        }),
        "error": (err) => console.error(err),
      });
  }

  public async resetServer(): Promise<void> {
    this.httpClient.post(
      `${environment.serverBaseUrl}/state/reset`,
      {}
    ).subscribe({
      error: err => console.error(err),
    });
  }

  public async updateScreenshotCaptureInterval(updateInterval: number): Promise<void> {
    this.httpClient.post(
      `${environment.serverBaseUrl}/screenshot/updateInterval`,
      {newInterval: updateInterval}
    ).subscribe({
      error: err => console.error(err),
    });
  }

  public async getIntervalSpeed(): Promise<void> {
    const intervalSpeed: number = await lastValueFrom(
      this.httpClient
        .get<number>(
          `${environment.serverBaseUrl}/screenshot/intervalSpeed`,
      {headers: this.headers}
        )
    );

    set((model) => {
      model.timer.screenshotCaptureInterval = intervalSpeed;
    });
  }

  public async getServerMetrics(): Promise<void> {
    const serverMetrics: ServerMetrics = await lastValueFrom(
      this.httpClient
        .get<ServerMetrics>(
        `${environment.serverBaseUrl}/state/system-metrics`,
        {headers: this.headers}
      )
    );

    set((model) => {
      model.serverMetrics = serverMetrics;
    });
  }

  public async getExamsFromServer(): Promise<void> {
    /*this.httpClient.get<Exam[]>(
      `${environment.serverBaseUrl}/exams`,
      {headers: this.headers})
      .subscribe({
        "next": (exams) => set((model) => {
          model.examData.exams = this.sortExams(exams);

          if (model.examData.exams.length >= 1) {
            model.examData.curExam = model.examData.exams[0];
          };
        }),
        "error": (err) => console.error(err),
      });*/

    let date = Date.now()

    set((model) => {
      model.examData.exams = [
        {
          id: 0,
          plannedStart: new Date(date),
          plannedEnd: new Date(date),
          actualEnd: new Date(date),
          title: "a1",
          pin: 123,
          state: ExamState.CREATED
        },
        {
          id: 1,
          plannedStart: new Date(new Date().setHours(3)),
          plannedEnd: new Date(date),
          actualEnd: new Date(date),
          title: "a2",
          pin: 123,
          state: ExamState.CREATED
        },
        {
          id: 2,
          plannedStart: new Date(date),
          plannedEnd: new Date(date),
          actualEnd: new Date(date),
          title: "a3",
          pin: 123,
          state: ExamState.CREATED
        },
        {
          id: 3,
          plannedStart: new Date(new Date(date).setHours(1)),
          plannedEnd: new Date(date),
          actualEnd: new Date(date),
          title: "a4",
          pin: 123,
          state: ExamState.CREATED
        },
      ];
      model.examData.exams = this.sortExams(model.examData.exams);

      if (model.examData.exams.length >= 1) {
        model.examData.curExam = model.examData.exams[0];
      };
    })
  }

  public async getExamByIdFromServer(id: number): Promise<void> {
    this.httpClient.get<Exam>(
      `${environment.serverBaseUrl}/exams/${id}`,
      {headers: this.headers})
      .subscribe({
        "next": (exam) => set((model) => {
          let index: number = model.examData.exams
            .findIndex((e) => e.id === id);

          if (index >= 0 && !Number.isNaN(index)) {
            model.examData.exams[index] = exam;
          }

          model.examData.exams = this.sortExams(model.examData.exams);
        }),
        "error": (err) => console.error(err),
      });
  }

  public async createNewExam(exam: ExamDto): Promise<void> {
    this.httpClient.post<Exam>(
      `${environment.serverBaseUrl}/exams`,
      exam
    ).subscribe({
      next: (exam) => {
        this.getExamsFromServer();
      },
      error: err => console.error(err)
    });
  }

  public async deleteExamByIdFromServer(id: number): Promise<void> {
    this.httpClient.delete(
      `${environment.serverBaseUrl}/exams/${id}`,
      {headers: this.headers})
      .subscribe({
        next: () => {
          this.getExamsFromServer();
        },
        error: (error) => console.log(error)
      });
  }

  private sortExams(exams: Exam[]): Exam[] {
    return exams.sort((a, b) => {
      if (a.plannedStart === b.plannedStart) {
        return (a.title < b.title) ? 1 : -1;
      }

      return (a.plannedStart < b.plannedStart) ? 1 : -1
    })
  };
}
