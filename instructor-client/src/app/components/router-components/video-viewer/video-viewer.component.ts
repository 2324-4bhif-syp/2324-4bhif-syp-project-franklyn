import {Component, inject} from '@angular/core';
import {StoreService} from "../../../services/store.service";
import {
  ExamineeDownloadListComponent
} from "../../examinee-lists/examinee-download-list/examinee-download-list.component";
import {VideoExamineeComponent} from "../../examinee-versions/video-examinee/video-examinee.component";

@Component({
  selector: 'app-video-viewer',
  standalone: true,
  imports: [
    ExamineeDownloadListComponent,
    VideoExamineeComponent
  ],
  templateUrl: './video-viewer.component.html',
  styleUrl: './video-viewer.component.css'
})
export class VideoViewerComponent {
  protected store = inject(StoreService).store;
}
