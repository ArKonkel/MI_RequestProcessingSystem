import type {Category} from "@/documentTypes/enums/Category.ts";
import type {Priority} from "@/documentTypes/enums/Priority.ts";
import type {ProcessItemCreateDtd} from "@/documentTypes/dtds/ProcessItemCreateDtd.ts";

export interface RequestCreateDtd {
  processItem: ProcessItemCreateDtd;
  priority: Priority | null; //must be null for formular init
  category: Category | null;
  customerId: number | null;
}
