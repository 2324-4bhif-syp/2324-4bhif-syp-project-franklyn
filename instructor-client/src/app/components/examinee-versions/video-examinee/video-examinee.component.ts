import {Component, inject, Input} from '@angular/core';
import {StoreService} from "../../../services/store.service";
import {Examinee} from "../../../model";
import {environment} from "../../../../../env/environment";

@Component({
  selector: 'app-video-examinee',
  standalone: true,
  imports: [],
  templateUrl: './video-examinee.component.html',
  styleUrl: './video-examinee.component.css'
})
export class VideoExamineeComponent {
  private store = inject(StoreService).store;

  @Input() examinee: Examinee | undefined;

  getVideoUrl(): string {
    return `${environment.serverBaseUrl}/video/${this.examinee?.firstname}-${this.examinee?.lastname}?cache=${this.store.value.cacheBuster.cachebustNum}`; //TODO: get new video url //examinee gets checked in the html
  }

  showVideo(): boolean {
    return this.examinee !== undefined &&
      this.store.value.patrol.patrolExaminee?.firstname === this.examinee.firstname &&
      this.store.value.patrol.patrolExaminee?.lastname === this.examinee.lastname;
  }
}
