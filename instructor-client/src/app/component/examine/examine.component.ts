import {Component, Injectable, Input} from '@angular/core';
import {Examine} from "../../shared/entity/Examine";
import {CommonModule} from "@angular/common";
import {environment} from "../../../environment/environment";
import ExamineDataService from "../../shared/repository/examine-data.service";

@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-examine',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './examine.component.html',
  styleUrl: './examine.component.css'
})
export class ExamineComponent {
  constructor(protected examineRepo: ExamineDataService) {
  }

  @Input() examine: Examine | undefined;
  @Input() showImage: boolean = false;

  getAddress(): string {
    let addressString: string = "";

    if (this.examine) {
      addressString =  "http://" + this.examine.ipAddress + ":" + environment.clientPort;
    }

    return addressString;
  }

  getScreenshotAddress() {
    return this.getAddress() + "/screenshot/?reloadNumber=" + this.examineRepo.getReloadNumber();
  }

  getActivity(): string {
    let activity: string = "";

    if (this.examine && !this.showImage && this.examine.connected) {
      activity =  "🟢";
    } else if (this.examine && !this.showImage){
      activity = "🔴";
    }

    return activity
  }
}
