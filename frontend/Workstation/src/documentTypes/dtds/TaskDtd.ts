import type {ProcessItemDtd} from "@/documentTypes/dtds/ProcessItemDtd.ts";
import type {CompetenceDtd} from "@/documentTypes/dtds/CompetenceDtd.ts";
import type {TaskStatus} from "@/documentTypes/types/TaskStatus.ts";
import type {Priority} from "@/documentTypes/types/Priority.ts";

export interface TaskDtd {
  processItem: ProcessItemDtd;
  estimatedTime: number;
  workingTime: number;
  dueDate: string | null;
  priority: Priority;
  status: TaskStatus,
  acceptanceCriteria: string;
  calendarEntryId: number | null;
  competences: CompetenceDtd[];
  blockerId: number | null;
  blockedId: number | null;
  referenceTaskId: number | null;
  // referencedByIds?: number[];
  requestId: number | null;
  projectId: number | null;
}
