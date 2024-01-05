import {Examinee} from "./Examinee";

export class ExamineeDto {
  public username: string;
  public ipAddresses: string[];
  public connected: boolean;

  constructor(username: string, ipAddresses: string[], connected: boolean) {
    this.username = username;
    this.ipAddresses = ipAddresses;
    this.connected = connected;
  }
}
