import {Component, inject} from '@angular/core';
import {StoreService} from "../../../services/store.service";
import {ExamineeListComponent} from "../../examinee-lists/examinee-list/examinee-list.component";
import {FormsModule} from "@angular/forms";
import {PatrolPageExamineeComponent} from "../../entity-components/patrol-page-examinee/patrol-page-examinee.component";

@Component({
  selector: 'app-patrol-mode',
  standalone: true,
    imports: [
        ExamineeListComponent,
        PatrolPageExamineeComponent,
        FormsModule
    ],
  templateUrl: './patrol-mode.component.html',
  styleUrl: './patrol-mode.component.css'
})
export class PatrolModeComponent {
  protected store = inject(StoreService).store;

  getPatrolModeOnState():string {
    let returnString: string = "off";

    if (this.store.value.patrol.isPatrolModeOn) {
      return "on";
    }

    return returnString;
  }

  getPatrolModeOnStateClass():string {
    let returnString: string = "text-danger";

    if (this.store.value.patrol.isPatrolModeOn) {
      return "text-success";
    }

    return returnString;
  }
}
