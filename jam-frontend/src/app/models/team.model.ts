import { Deserializable } from "./deserializable";
import { PlayerModel } from "./player.model";

export class TeamModel implements Deserializable {

  id?: string;
  name?: string;
  players: PlayerModel[] = [];
  totalScore?: number;

  deserialize(input: any): this {
    Object.assign(this, input);
    this.players = input.players
      .map((player: PlayerModel) => new PlayerModel().deserialize(player));
    return this;
  }
  
}