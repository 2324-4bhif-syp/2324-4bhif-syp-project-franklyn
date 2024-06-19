import {inject, Injectable} from '@angular/core';
import {WebApiService} from "./web-api.service";
import {StoreService} from "./store.service";
import {Location} from "@angular/common";
import {Examinee, set} from "../model";

@Injectable({
  providedIn: 'root'
})
export class ExamineeService {
  private store = inject(StoreService).store;
  private location = inject(Location);

  constructor(private webApi: WebApiService) {
    this.webApi.getExamineesFromServer(0); //TODO: For exam
  }

  resetExaminees(): void {
    this.webApi.resetServer();

    set((model) => {
      model.examineeData.examinees = [];
    })
  }

  get(predicate?: ((item: Examinee) => boolean) | undefined): Examinee[] {
    if (predicate) return this.get().filter(predicate);
    return this.store.value.examineeData.examinees;
  }

  updateScreenshots() {
    if (this.location.path() !== "/video-viewer") {
      set((model) => {
        model.cacheBuster.cachebustNum++;
      });
    }
  }

  newPatrolExaminee(examinee?: Examinee, ignoreConnection: boolean = false) {
    // if a valid examinee is specified to be the patrol-examinee
    if (examinee !== undefined && (examinee.isConnected || ignoreConnection)) {
      set((model) => {
        model.patrol.isPatrolModeOn = false;
        model.patrol.patrolExaminee = examinee;
      });
    } else {
      /*
      * potential new valid patrol examinees
      * (they are connected and not the current patrol-examinee)
      */
      let examinees: Examinee[] = this.get(e => e?.isConnected && e.firstname !== this.store.value.patrol.patrolExaminee?.firstname && e.lastname !== this.store.value.patrol.patrolExaminee?.lastname);

      // if length = 0 then there are no valid patrol-examinees
      if (examinees.length === 0) {
        /*
        * 1) if the patrol examinee is not connected we set it
        * to undefined because then we have no possible new
        * patrol-examinee.
        * 2) on the other hand, if we already do have a valid
        * patrol-examinee and no other possible valid new
        * patrol examinees (as specified in the if-statement
        * above), then we can just stay with the current one
        * 3) this also sets the patrol-examinee to undefined if
        * the patrol-mode isn't on and the current patrol examinee
        * isn't connected so this is a way to check if the
        * current chosen (patrol) examinee is still connected
        */
        if (!this.store.value.patrol.patrolExaminee?.isConnected) {
          set((model) => {
            model.patrol.patrolExaminee = undefined;
          });
        }
      } else if (this.store.value.patrol.isPatrolModeOn && examinees.length !== 0) {
        /*
        * if there are any valid examinees, that could become
        * the new patrol-examinee and if the patrol-mode is
        * on then we choose a new random patrol-examinee
        */
        set((model) => {
          model.patrol.patrolExaminee = examinees[Math.floor(Math.random() * examinees.length)];
        })
      }
    }
  }
}
