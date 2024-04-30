import {Component, inject, Input} from '@angular/core';
import {ExamineeService} from "../../../services/examinee.service";
import {StoreService} from "../../../services/store.service";
import {Examinee} from "../../../model";
import {environment} from "../../../../../env/environment";

@Component({
  selector: 'app-patrol-page-examinee',
  standalone: true,
  imports: [],
  templateUrl: './patrol-page-examinee.component.html',
  styleUrl: './patrol-page-examinee.component.css'
})
export class PatrolPageExamineeComponent {
  protected examineeSvc = inject(ExamineeService);
  protected store = inject(StoreService).store;

  @Input() examinee: Examinee | undefined;
  @Input() showImage: boolean = false;

  getScreenshotAddress() {
    return `${environment.serverBaseUrl}/screenshot/${this.examinee!.username}/${environment.imageWidth}/${environment.imageHeight}?cachebust=${this.store.value.cacheBuster.cachebustNum}`;
  }

  getActivity(): string {
    let activity: string = "";

    if (this.examinee && !this.showImage && this.examinee.connected) {
      activity =  "btn-success";
    } else if (this.examinee && !this.showImage){
      activity = "btn-danger";
    }

    return activity
  }

  selectExaminee() {
    this.examineeSvc.newPatrolExaminee(this.examinee);
  }
}
