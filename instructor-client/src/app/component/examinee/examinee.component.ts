import {Component, Injectable, Input} from '@angular/core';
import {Examinee} from "../../shared/entity/Examinee";
import {CommonModule} from "@angular/common";
import {environment} from "../../../../env/environment";
import ExamineeDataService from "../../shared/repository/examinee-data.service";

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
  constructor(protected examineeRepo: ExamineeDataService) {
  }

  @Input() examinee: Examinee | undefined;
  @Input() showImage: boolean = false;
  private count: number = 0;

  getScreenshotAddress() {
    this.count++;

    return `${environment.serverBaseUrl}/screenshot/${this.examinee!.username}/${environment.imageWidth}/${environment.imageHeight}?cachebust=${this.count}`;
  }

  getActivity(): string {
    let activity: string = "";

    if (this.examinee && !this.showImage && this.examinee.connected) {
      activity =  "🟢";
    } else if (this.examinee && !this.showImage){
      activity = "🔴";
    }

    return activity
  }

  selectExaminee() {
    this.examineeRepo.newPatrolExaminee(this.examinee);
  }
}
