import {Component, inject} from '@angular/core';
import {StoreService} from "../../../services/store.service";
import {
  ExamineeDownloadListComponent
} from "../../entity-lists/examinee-download-list/examinee-download-list.component";
import {VideoExamineeComponent} from "../../entity-components/video-examinee/video-examinee.component";

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
