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
    this.webApi.getExamineesFromServer();
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
        model.cacheBuster.cacheBusterNum++;
      });
    }
  }

  newPatrolExaminee(examinee?: Examinee, ignoreConnection: boolean = false) {
    if (examinee !== undefined && (examinee.connected || ignoreConnection)) {
      set((model) => {
        model.patrol.patrolModeOn = false;
        model.patrol.patrolExaminee = examinee;
      });
    } else if (this.store.value.patrol.patrolModeOn) {
      let examinees: Examinee[] = this.get(e => e?.connected && e.username !== this.store.value.patrol.patrolExaminee?.username);

      if (this.get().length !== 0) {
        // no examinees connected
        if (examinees.length === 0) {
          set((model) => {
            model.patrol.patrolExaminee = undefined;
          });
        } else {
          set((model) => {
            model.patrol.patrolExaminee = examinees[Math.floor(Math.random() * examinees.length)];
          })
        }
      }
    }

    if (this.get().length === 0) {
      set((model) => {
        model.patrol.patrolExaminee = undefined;
      });
    }
  }
}
