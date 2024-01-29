import { Component } from '@angular/core';
import {ExamineeComponent} from "../examinee/examinee.component";
import {NgForOf} from "@angular/common";
import ExamineeDataService from "../../shared/repository/examinee-data.service";
import {DownloadExamineeComponent} from "../download-examinee/download-examinee.component";
import {environment} from "../../../../env/environment";

@Component({
  selector: 'app-examinee-download-list',
  standalone: true,
  imports: [
    ExamineeComponent,
    NgForOf,
    DownloadExamineeComponent
  ],
  templateUrl: './examinee-download-list.component.html',
  styleUrl: './examinee-download-list.component.css'
})
export class ExamineeDownloadListComponent {
  constructor(protected examineeRepo: ExamineeDataService) {
  }

  getDownloadUrl(): string {
    return `${environment.recorderBaseUrl}video/download`
  }
}
