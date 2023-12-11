import {Component, Input} from '@angular/core';
import {Examine} from "../../shared/entity/Examine";
import {CommonModule} from "@angular/common";
import {environment} from "../../../environment/environment";

@Component({
  selector: 'app-examine',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './examine.component.html',
  styleUrl: './examine.component.css'
})
export class ExamineComponent {
  @Input() examine: Examine | undefined;
  @Input() showImage: boolean = false;

  getAddress(): string {
    let addressString: string = "";

    if (this.examine) {
      addressString =  "http://" + this.examine.ipAddress + environment.clientPort;
    }

    return addressString;
  }
}
