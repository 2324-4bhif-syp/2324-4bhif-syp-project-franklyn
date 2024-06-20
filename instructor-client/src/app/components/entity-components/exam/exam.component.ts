import {Component, inject, Input} from '@angular/core';
import {Exam} from "../../../model/entity/Exam";
import {ExamService} from "../../../services/exam.service";

@Component({
  selector: 'app-exam',
  standalone: true,
  imports: [],
  templateUrl: './exam.component.html',
  styleUrl: './exam.component.css'
})
export class ExamComponent {
  private examSvc = inject(ExamService);

  @Input() exam: Exam | undefined;

  protected setExamToCurExam() {
    if (this.exam) {
      this.examSvc.setCurExam(this.exam);
    }
  }

  getDateFromExam(): string {
    if (!this.exam)
      return "";

    return (this.exam.plannedStart.getMonth() + 1) + '.' +
      this.exam.plannedStart.getDate() + '.' +
      this.exam.plannedStart.getFullYear();
  }

  getTimeFromExam(): string {
    if (!this.exam)
      return "";

    return this.exam.plannedStart.getHours() + ":" + this.exam.plannedStart.getMinutes();
  }
}
