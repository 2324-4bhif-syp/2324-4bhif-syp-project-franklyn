import { Injectable } from "@angular/core";
import { Examinee } from "../entity/Examinee";
import {WebApiService} from "../web-api.service";
import {ExamineeDto} from "../entity/ExamineeDto";
import {ExamineeDtoFilterService} from "./examinee-dto-filter.service";
import {connectable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export default class ExamineeDataService {
  constructor(private webApi: WebApiService, private examineeDtoService: ExamineeDtoFilterService) {
    this.getAllExamineesFromServer();
  }

  private items: Examinee[] = [];
  private patrolExaminee: Examinee | undefined;
  private examineeIpCache: Map<string, [Examinee, number]> = new Map();

  patrolModeOn: boolean = false;

  //updates the list of examinees via the server
  getAllExamineesFromServer(): void {
    this.webApi.getClientsFromServer().subscribe({
      next: (examineeDtos) => {
        this.handleExamineeDtos(examineeDtos);
      },
      error: (err) => console.error(err),
    });
  }

  addExamineeIpToCache(examineeDto: ExamineeDto, idx: number): void {
    this.examineeDtoService.determineIpForExaminees(examineeDto).subscribe({
      next: (examinee) => {
        if (examinee) {
          // Add to the list as well if it is not contained
          if (idx == -1) {
            idx = this.items.length;
            this.items.push(examinee);
          }

          this.examineeIpCache.set(examinee.username, [examinee, idx]);
          this.items[idx].connected = examinee.connected;
        }
      },
      error: (_) => _,
    })
  }

  handleExamineeDtos(examineeDtos: ExamineeDto[]): void {
    for (const examineeDto of examineeDtos) {
      // [0] Examinee, [1] idx
      const cached = this.examineeIpCache.get(examineeDto.username);

      // In cache but disconnected => so it has to be in the list, therefore update connected state
      if (cached && !examineeDto.connected) {
        this.items[cached[1]].connected = false;

      // not in cache and not disconnected => check if it is in the list if not insert
      } else if (!cached && !examineeDto.connected) {
        if (this.items.findIndex((e) => e.username === examineeDto.username) == -1) {
          this.items.push(new Examinee(examineeDto.username, "null", false));
        }

      // not in cache but connected => update cache
      } else if (examineeDto.connected && (!cached || cached && !this.items[cached[1]].connected)) {
        const idx: number = this.items.findIndex((e) => e.username === examineeDto.username);
        this.addExamineeIpToCache(examineeDto, idx);
      }
    }
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
