import type {CommentCreateDtd} from "@/documentTypes/dtds/CommentCreateDtd.ts";
import axios from "axios";

export async function addCommentToRequest(requestId: number,  comment: CommentCreateDtd) {
  const response = await axios.post(`/api/processItems/${requestId}/comments`, comment);
  return response.data;
}
