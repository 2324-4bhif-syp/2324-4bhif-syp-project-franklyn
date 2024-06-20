import {Component, inject} from '@angular/core';
import {StoreService} from "../../../services/store.service";
import {distinctUntilChanged, map} from "rxjs";
import {AsyncPipe} from "@angular/common";
import {ExamComponent} from "../../entity-components/exam/exam.component";

@Component({
  selector: 'app-exam-selection-list',
  standalone: true,
  imports: [
    AsyncPipe,
    ExamComponent
  ],
  templateUrl: './exam-selection-list.component.html',
  styleUrl: './exam-selection-list.component.css'
})
export class ExamSelectionListComponent {
  protected exams = inject(StoreService)
    .store
    .pipe(
      map(model => model.examData.exams),
      distinctUntilChanged()
    );
}
