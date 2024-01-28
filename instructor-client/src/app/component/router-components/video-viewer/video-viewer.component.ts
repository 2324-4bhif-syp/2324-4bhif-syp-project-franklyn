import { Component } from '@angular/core';
import {CommonModule} from "@angular/common";
import {ExamineeListComponent} from "../../examinee-list/examinee-list.component";
import {FormsModule} from "@angular/forms";
import ExamineeDataService from "../../../shared/repository/examinee-data.service";
import {environment} from "../../../../../env/environment";
import {ExamineeComponent} from "../../examinee/examinee.component";
import {ExamineeVideoComponent} from "../../examinee-video/examinee-video.component";

@Component({
  selector: 'app-video-viewer',
  standalone: true,
  imports: [
    CommonModule,
    ExamineeListComponent,
    FormsModule,
    ExamineeComponent,
    ExamineeVideoComponent
  ],
  templateUrl: './video-viewer.component.html',
  styleUrl: './video-viewer.component.css'
})
export class VideoViewerComponent {
  constructor(protected examineeRepo: ExamineeDataService) {
  }
}
