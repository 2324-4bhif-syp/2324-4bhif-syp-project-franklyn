import { Injectable } from "@angular/core";
import { Examinee } from "../entity/Examinee";
import {WebApiService} from "../web-api.service";
import {CacheBusterService} from "./cache-buster.service";
import {Location} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export default class ExamineeDataService {
  constructor(private webApi: WebApiService, private cacheBusterService: CacheBusterService, private location: Location) {
    this.getAllExamineesFromServer();
  }

  private items: Examinee[] = [];
  private patrolExaminee: Examinee | undefined;

  patrolModeOn: boolean = false;

  //updates the list of examinees via the server
  getAllExamineesFromServer(): void {
    this.webApi.getClientsFromServer().subscribe({
      next: (examinees) => this.items = examinees,
      error: (err) => console.error(err),
    });
  }

  resetExaminees(): void {
    this.webApi.resetExaminees();
    this.items = [];
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
    if (this.location.path() !== "/video-viewer") {
      this.cacheBusterService.cacheBusterNum++;
    }

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

}
