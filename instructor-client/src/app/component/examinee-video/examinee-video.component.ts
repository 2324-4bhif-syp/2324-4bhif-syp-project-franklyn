import {Component, Input, OnInit} from '@angular/core';
import {Examinee} from "../../shared/entity/Examinee";
import {CommonModule} from "@angular/common";
import {environment} from "../../../../env/environment";

@Component({
  selector: 'app-examinee-video',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './examinee-video.component.html',
  styleUrl: './examinee-video.component.css'
})
export class ExamineeVideoComponent{
  @Input() examinee: Examinee | undefined;

  getVideoUrl(): string {
    return `${environment.recorderBaseUrl}video/${this.examinee?.ipAddress}`; //examinee gets checked in the html
  }
}
