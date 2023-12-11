import {Component, Injectable, Input} from '@angular/core';
import {Examinee} from "../../shared/entity/Examinee";
import {CommonModule} from "@angular/common";
import {environment} from "../../../environment/environment";
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

  getAddress(): string {
    let addressString: string = "";

    if (this.examinee) {
      addressString =  "http://" + this.examinee.ipAddress + ":" + environment.clientPort;
    }

    return addressString;
  }

  getScreenshotAddress() {
    return this.getAddress() + "/screenshot/?reloadNumber=" + this.examineeRepo.getReloadNumber();
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
}
