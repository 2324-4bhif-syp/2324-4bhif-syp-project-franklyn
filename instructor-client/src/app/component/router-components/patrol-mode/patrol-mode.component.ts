import { Component } from '@angular/core';
import {ExamineeComponent} from "../../examinee/examinee.component";
import {ExamineeListComponent} from "../../examinee-list/examinee-list.component";
import {FormsModule} from "@angular/forms";
import ExamineeDataService from "../../../shared/repository/examinee-data.service";
import {environment} from "../../../../../env/environment";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-patrol-mode',
  standalone: true,
    imports: [
        CommonModule,
        ExamineeComponent,
        ExamineeListComponent,
        FormsModule
    ],
  templateUrl: './patrol-mode.component.html',
  styleUrl: './patrol-mode.component.css'
})
export class PatrolModeComponent {
  constructor(protected examineeRepo: ExamineeDataService) {
    setInterval(() => {
      examineeRepo.getAllExamineesFromServer();
      examineeRepo.newPatrolExaminee();
    }, environment.nextClientScheduleTime);
  }
}
