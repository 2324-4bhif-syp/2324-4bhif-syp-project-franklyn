import {Component, HostListener, inject, Input} from '@angular/core';
import {ExamineeService} from "../../../services/examinee.service";
import {StoreService} from "../../../services/store.service";
import {Examinee, set} from "../../../model";
import {environment} from "../../../../../env/environment";
import {distinctUntilChanged, map} from "rxjs";
import {AsyncPipe} from "@angular/common";

@Component({
    selector: 'app-patrol-page-examinee',
  imports: [
    AsyncPipe
  ],
    templateUrl: './patrol-page-examinee.component.html',
    styleUrl: './patrol-page-examinee.component.css'
})
export class PatrolPageExamineeComponent {
  protected examineeSvc = inject(ExamineeService);
  protected store = inject(StoreService).store;

  @Input() examId: number | undefined;
  @Input() examinee: Examinee | undefined;
  @Input() showImage: boolean = false;

  ngOnInit() {
    setInterval(() => {
      console.log("checking clipboard!");
      if (typeof this.examId === "number" && this.examinee !== undefined) {
        this.examineeSvc.getSusnessOfExaminee(
          this.examinee.id,
          this.examinee.firstname,
          this.examinee.lastname,
          this.examId
        );
      }
    }, 5000);
  }

  protected readonly isFullScreen = this.store.pipe(
    map(state => state.patrolModeModel.isFullScreen),
    distinctUntilChanged()
  );

  getScreenshotAddress() {
    return `${environment.serverBaseUrl}/telemetry/by-user/${this.examinee!.id}/${this.examId}/screen/download?cachebust=${this.store.value.patrolModeModel.cacheBuster.cachebustNum}`;
  }

  getActivity(): string {
    let activity: string = "";

    if (this.examinee && !this.showImage && this.examinee.isConnected) {
      activity =  "btn-success";
    } else if (this.examinee && !this.showImage){
      activity = "btn-danger";
    }

    return activity
  }

  selectExaminee() {
    this.examineeSvc.newPatrolExaminee(this.examinee);
  }

  openModal() {
    set(model => {
      model.patrolModeModel.isFullScreen = true;
    });
  }

  closeModal() {
    set(model => {
      model.patrolModeModel.isFullScreen = false;
    });
  }

  @HostListener('document:keydown.escape', ['$event'])
  handleEscape() {
    this.closeModal();
  }
}
