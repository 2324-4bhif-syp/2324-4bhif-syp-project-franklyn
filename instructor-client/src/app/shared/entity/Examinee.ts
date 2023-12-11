export class Examinee {
  constructor(userName: string, ipAddress: string, connected:boolean) {
    this.userName = userName;
    this.ipAddress = ipAddress;
    this.connected = connected;
  }

  userName: string;
  ipAddress: string;
  connected:boolean;
}
