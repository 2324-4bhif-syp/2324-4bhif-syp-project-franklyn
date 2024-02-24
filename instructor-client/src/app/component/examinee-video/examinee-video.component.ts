import {Component, Input} from '@angular/core';
import {Examinee} from "../../shared/entity/Examinee";
import {CommonModule} from "@angular/common";
import {environment} from "../../../../env/environment";
import ExamineeDataService from "../../shared/repository/examinee-data.service";

@Component({
  selector: 'app-examinee-video',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './examinee-video.component.html',
  styleUrl: './examinee-video.component.css'
})
export class ExamineeVideoComponent{
  constructor(private examineeRepo: ExamineeDataService) {
  }

  @Input() examinee: Examinee | undefined;

  getVideoUrl(): string {
    return `${environment.serverBaseUrl}/video/${this.examinee?.username}`; //examinee gets checked in the html
  }

  showVideo():boolean {
    return this.examinee !== undefined && this.examineeRepo.getPatrolExaminee()?.username === this.examinee.username;
  }
}
