import { Component } from '@angular/core';
import {ExamineeComponent} from "../../examinee/examinee.component";
import {ExamineeListComponent} from "../../examinee-list/examinee-list.component";
import {FormsModule} from "@angular/forms";
import ExamineeDataService from "../../../shared/repository/examinee-data.service";
import {environment} from "../../../../../env/environment";
import {CommonModule} from "@angular/common";
import {PatrolManagerService} from "../../../shared/repository/patrol-manager.service";
import {ExamineeDownloadListComponent} from "../../examinee-download-list/examinee-download-list.component";

@Component({
  selector: 'app-patrol-mode',
  standalone: true,
    imports: [
        CommonModule,
        ExamineeComponent,
        ExamineeListComponent,
        FormsModule,
        ExamineeDownloadListComponent
    ],
  templateUrl: './patrol-mode.component.html',
  styleUrl: './patrol-mode.component.css'
})
export class PatrolModeComponent {
  constructor(protected examineeRepo: ExamineeDataService, protected patrolManagerService: PatrolManagerService) {
  }

  public intervalSpeed: number = 10;

  resetExaminees(_event: Event): void {
    this.examineeRepo.resetExaminees();
  }

  screenshotCaptureIntervalUpdate(_: Event): void {
    this.examineeRepo.updateScreenshotCaptureInterval(this.intervalSpeed);
  }

  protected readonly environment = environment;
}
