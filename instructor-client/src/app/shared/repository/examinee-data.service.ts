import { Injectable } from "@angular/core";
import { Examinee } from "../entity/Examinee";
import {WebApiService} from "../web-api.service";

@Injectable({
  providedIn: 'root'
})
export default class ExamineeDataService {
  constructor(private webApi: WebApiService) {
    this.getAllExamineesFromServer();
  }

  private items: Examinee[] = [];
  private patrolExaminee: Examinee | undefined;
  private cachebustNumVideo: number = 0;
  private cachebustNumImage: number = 0;

  patrolModeOn: boolean = false;

  //updates the list of examinees via the server
  getAllExamineesFromServer(): void {
    this.webApi.getClientsFromServer().subscribe({
      next: (examinees) => this.items = examinees,
      error: (err) => console.error(err),
    });
  }

  get(predicate?: ((item: Examinee) => boolean) | undefined): Examinee[] {
    if (predicate) return this.get().filter(predicate);
    return this.items;
  }

  getPatrolExaminee(): Examinee | undefined {
    if (this.items.length === 0) {
      this.patrolExaminee = undefined;
    }

    return this.patrolExaminee;
  }

  unsetPatrolExaminee() {
    this.patrolExaminee = undefined;
  }

  newPatrolExaminee(examinee?: Examinee, ignoreConnection: boolean = false) {
    this.cacheBusterNumImg += 1;

    if (examinee !== undefined && (examinee.connected || ignoreConnection)) {
      this.patrolModeOn = false;
      this.patrolExaminee = examinee;
    } else if (this.patrolModeOn) {
      let examinees: Examinee[] = this.get(e => e?.connected && e.username !== this.patrolExaminee?.username);

      if (this.items.length !== 0) {
        if (examinees.length === 0) {
          this.patrolExaminee = this.items[0];
        } else {
          this.patrolExaminee = examinees[Math.floor(Math.random() * examinees.length)];
        }
      }
    }

    if (this.items.length === 0) {
      this.patrolExaminee = undefined;
    }
  }

  get cacheBusterNumVideo(): number {
    return this.cachebustNumVideo;
  }

  set cacheBusterNumVideo(val: number) {
    if (val === Number.MAX_VALUE) {
      this.cachebustNumVideo = 0;
    } else {
      this.cachebustNumVideo = val;
    }
  }

  get cacheBusterNumImg(): number {
    return this.cachebustNumImage;
  }

  set cacheBusterNumImg(val: number) {
    if (val === Number.MAX_VALUE) {
      this.cachebustNumImage = 0;
    } else {
      this.cachebustNumImage = val;
    }
  }

}
