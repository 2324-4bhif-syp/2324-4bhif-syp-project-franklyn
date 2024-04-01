import {Component, Injectable, Input} from '@angular/core';
import {Examinee} from "../../shared/entity/Examinee";
import {CommonModule} from "@angular/common";
import {environment} from "../../../../env/environment";
import ExamineeDataService from "../../shared/repository/examinee-data.service";
import {CacheBusterService} from "../../shared/repository/cache-buster.service";

@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-examinee',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './examinee.component.html',
  styleUrl: './examinee.component.css'
})
export class ExamineeComponent {
  constructor(protected examineeRepo: ExamineeDataService, private cacheBusterService: CacheBusterService) {
  }

  @Input() examinee: Examinee | undefined;
  @Input() showImage: boolean = false;

  getScreenshotAddress() {
    return `${environment.serverBaseUrl}/screenshot/${this.examinee!.username}/${environment.imageWidth}/${environment.imageHeight}?cachebust=${this.cacheBusterService.cacheBusterNum}`;
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
    this.examineeRepo.newPatrolExaminee(this.examinee);
  }
}
