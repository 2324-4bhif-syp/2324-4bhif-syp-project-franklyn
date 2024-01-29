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
    for (let e of this.items) {
      console.log(`Examinee: ${e.username}`);
    }
    for (const dto of dtos) {
      //    cached = [0] Examinee, [1] idx
      const cached = this.examineeIpCache.get(dto.username);
      console.log("NEXT");
      console.log("====");
      console.log("|");
      console.log("v");
      console.log(dto);

      if (cached) {
        this.handleCachedExaminee(cached[0], cached[1], dto);
      } else if (dto.connected) {
        const idx = this.items.findIndex((e) => e.username === dto.username);
        let i = 0;
        console.log("cur: " + idx);
        console.log("dto");
        console.log(dto);
        for (const e of this.items) {
          console.log(++i + ": " + e.username);
        }
        console.log();
        console.log("IDX: " + idx);
        this.addExamineeIpToCache(dto, idx);
      } else if (!this.items.find((e) => e.username === dto.username)) {
        console.log("DONT YOU DARE");
        this.items.push({username: dto.username, ipAddress: "", connected: false});
      } else {
        console.log("nuthing burgur");
      }
    }
  }

  handleCachedExaminee(examinee: Examinee, idx: number, dto: ExamineeDto) {
    console.log("RAND: " + idx);
    console.log(this.items);
    if (examinee.connected && !dto.connected) {
      this.items[idx].username = examinee.username;
      this.items[idx].connected = false;
      this.items[idx].ipAddress = "";
      this.examineeIpCache.delete(examinee.username);
    }
  }

  addExamineeIpToCache(examineeDto: ExamineeDto, idx: number): void {
    console.log("FIRST LOG");
    console.log(idx);

    this.examineeDtoService.determineIpForExaminees(examineeDto).subscribe({
      next: (examinee) => {
        console.log("MED LOG");
        console.log(idx);

        console.log(examinee);

        if (!examinee || this.examineeIpCache.get(examinee.username)) {
          return;
        }

        let nidx = this.items.findIndex(e => e.username === examinee.username);
        console.log("LIST");
        console.log(nidx);
        console.log(this.items);
        console.log();

        console.log("SEC LOG");
        console.log(idx);
        if (nidx == -1) {
          console.log("I am funny : " + idx);
          nidx = this.items.length;
          console.log("very very funny : " + idx);
          this.items.push(examinee);
        }

        console.log("TO CACHE");
        console.log(idx);
        console.log(examinee);

        this.examineeIpCache.set(examinee.username, [examinee, nidx]);
        this.items[nidx] = examinee;
      },
      error: (_) => _,
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
