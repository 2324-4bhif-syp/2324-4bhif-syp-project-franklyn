import {Component, inject} from '@angular/core';
import {StoreService} from "../../../services/store.service";
import {ExamineeListComponent} from "../../examinee-lists/examinee-list/examinee-list.component";
import {PatrolPageExamineeComponent} from "../../examinee-versions/patrol-page-examinee/patrol-page-examinee.component";
import {FormsModule} from "@angular/forms";

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
}
