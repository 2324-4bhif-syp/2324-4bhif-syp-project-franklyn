import {Component, inject, Input} from '@angular/core';
import {ExamService} from "../../../services/exam.service";
import {Exam} from "../../../model/entity/Exam";
import {ExamState} from "../../../model/entity/Exam-State";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {set} from "../../../model";

@Component({
  selector: 'app-exam-dashboard',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './exam-dashboard.component.html',
  styleUrl: './exam-dashboard.component.css'
})
export class ExamDashboardComponent {
  protected examSvc = inject(ExamService);

  @Input() exam: Exam | undefined;

  getDateFromExam(): string {
    if (!this.exam)
      return "";

    return (this.exam.plannedStart.getMonth() + 1) + '.' +
      this.exam.plannedStart.getDate() + '.' +
      this.exam.plannedStart.getFullYear();
  }

  getFormattedTimeFrom(date: Date): string {
    return date.getHours() + ":" + date.getMinutes();
  }

  protected readonly ExamState = ExamState;
}
