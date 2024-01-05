import { Injectable } from "@angular/core";
import { Examinee } from "../entity/Examinee";
import {WebApiService} from "../web-api.service";
import {ExamineeDto} from "../entity/ExamineeDto";
import {ExamineeDtoFilterService} from "./examinee-dto-filter.service";

@Injectable({
  providedIn: 'root'
})
export default class ExamineeDataService {
  constructor(private webApi: WebApiService, private examineeDtoService: ExamineeDtoFilterService) {
    this.getAllExamineesFromServer();
  }

  private items: Examinee[] = [];
  private patrolExaminee: Examinee | undefined;
  private examineeMap: Map<string, Examinee> = new Map();

  patrolModeOn: boolean = false;

  //updates the list of examinees via the server
  getAllExamineesFromServer(): void {
    this.webApi.getClientsFromServer().subscribe({
      next: (examineeDtos) => {
        for (const examineeDto of examineeDtos) {
          if (!this.examineeMap.get(examineeDto.username)) {
            this.examineeDtoService.determineIpForExaminees(examineeDto).subscribe({
              next: (examinee) => {
                if (examinee) {
                  this.examineeMap.set(examinee.username, examinee);
                  this.items.push(examinee);
                }
              },
              error: (err) => console.error(err),
            })
          }
        }
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
  }
}
