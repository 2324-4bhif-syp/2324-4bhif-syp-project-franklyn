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
      next: (dtos) => this.handleExamineeDtos(dtos),
      error: (err) => console.error(err),
    });
  }

  handleExamineeDtos(dtos: ExamineeDto[]): void {
    for (const dto of dtos) {
      //    cached = [0] Examinee, [1] idx
      const cached = this.examineeIpCache.get(dto.username);

      if (cached) {
        this.handleCachedExaminee(cached[0], cached[1], dto);
      } else if (dto.connected) {
        this.addExamineeIpToCache(dto, this.items.findIndex(e => e.username === dto.username));
      } else if (!this.items.find(e => e.username === dto.username)) {
        this.items.push({username: dto.username, ipAddress: "", connected: false});
      }
    }
  }

  handleCachedExaminee(examinee: Examinee, idx: number, dto: ExamineeDto) {
    if (examinee.connected && !dto.connected) {
      this.items[idx] = {username: examinee.username, connected: false, ipAddress: ""};
      this.examineeIpCache.delete(examinee.username);
    }
  }

  addExamineeIpToCache(examineeDto: ExamineeDto, idx: number): void {
    this.examineeDtoService.determineIpForExaminees(examineeDto, idx).subscribe({
      next: (result) => {
        if (!result|| this.examineeIpCache.get(result[0].username)) {
          return;
        }
        let [examinee, idx] = result;

        if (idx == -1) {
          idx = this.items.length;
          this.items.push(examinee);
        }

        this.examineeIpCache.set(examinee.username, [examinee, idx]);
        this.items[idx] = examinee;
      },
    })
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
