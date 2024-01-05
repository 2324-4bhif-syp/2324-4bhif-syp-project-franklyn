export class Examinee {
  username: string;
  ipAddress: string;
  connected: boolean;

  constructor(username: string, ipAddress: string, connected: boolean) {
    this.username = username;
    this.ipAddress = ipAddress;
    this.connected = connected;
  }
}
