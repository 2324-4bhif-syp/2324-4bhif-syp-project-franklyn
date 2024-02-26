import {Component, Input} from '@angular/core';
import ExamineeDataService from "../../shared/repository/examinee-data.service";
import {Examinee} from "../../shared/entity/Examinee";
import {CommonModule} from "@angular/common";
import {environment} from "../../../../env/environment";
import {CacheBusterService} from "../../shared/repository/cache-buster.service";

@Component({
  selector: 'app-download-examinee',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './download-examinee.component.html',
  styleUrl: './download-examinee.component.css'
})
export class DownloadExamineeComponent {
  constructor(protected examineeRepo: ExamineeDataService, private cacheBusterService: CacheBusterService) {
  }

  @Input() examinee: Examinee | undefined;

  showVideoOfExaminee() {
    this.examineeRepo.newPatrolExaminee(this.examinee, true);
    this.cacheBusterService.cacheBusterNum++;
  }

  getDownloadUrl(): string {
    return `${environment.serverBaseUrl}/video/download/${this.examinee?.username}`
  }
}
