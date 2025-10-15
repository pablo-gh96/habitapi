export class CommentResponse {
  id!: number;
  message!: string;
  createdAt!: string;     // ISO: 2025-10-13T12:34:56
  fromUserName!: string;
  toUserName!: string;
}