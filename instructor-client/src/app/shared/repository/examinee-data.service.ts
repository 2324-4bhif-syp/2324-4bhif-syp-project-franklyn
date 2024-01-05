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

  protected items: Examinee[] = [];
  protected patrolExaminee: Examinee | undefined;
  protected reloadNumber: number = 0;
  patrolModeOn: boolean = false;

  //updates the list of examinees via the server
  getAllExamineesFromServer(): void {
    this.webApi.getClientsFromServer().subscribe({
      next: (examinees) => {
        this.items = examinees;
      },
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

  newPatrolExaminee(examinee?: Examinee) {
    if (examinee !== undefined && examinee.connected) {
      this.patrolModeOn = false;
      this.patrolExaminee = examinee;
    } else if (this.patrolModeOn) {
      let examinees: Examinee[] = this.get(e => e?.connected && e.ipAddress !== this.patrolExaminee?.ipAddress);

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

    this.reloadNumber++;
  }

  getReloadNumber(): number {
    return this.reloadNumber
  }
}