import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive} from "@angular/router";
import {
  ExamineeDownloadListComponent
} from "../../entity-lists/examinee-download-list/examinee-download-list.component";
import {VideoExamineeComponent} from "../../entity-components/video-examinee/video-examinee.component";

@Component({
  selector: 'app-edit-test-view',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    ExamineeDownloadListComponent,
    VideoExamineeComponent
  ],
  templateUrl: './edit-test-view.component.html',
  styleUrl: './edit-test-view.component.css'
})
export class EditTestViewComponent {

}
