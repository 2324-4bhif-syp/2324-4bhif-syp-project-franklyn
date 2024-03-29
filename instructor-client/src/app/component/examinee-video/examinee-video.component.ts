import {Component, Input} from '@angular/core';
import {Examinee} from "../../shared/entity/Examinee";
import {CommonModule} from "@angular/common";
import {environment} from "../../../../env/environment";
import ExamineeDataService from "../../shared/repository/examinee-data.service";
import {CacheBusterService} from "../../shared/repository/cache-buster.service";

@Component({
  selector: 'app-examinee-video',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './examinee-video.component.html',
  styleUrl: './examinee-video.component.css'
})
export class ExamineeVideoComponent{
  constructor(private examineeRepo: ExamineeDataService, private cacheBusterService: CacheBusterService) {
  }

  @Input() examinee: Examinee | undefined;

  getVideoUrl(): string {
    return `${environment.serverBaseUrl}/video/${this.examinee?.username}?cache=${this.cacheBusterService.cacheBusterNum}`; //examinee gets checked in the html
  }

  showVideo():boolean {
    return this.examinee !== undefined && this.examineeRepo.getPatrolExaminee()?.username === this.examinee.username;
  }
}
