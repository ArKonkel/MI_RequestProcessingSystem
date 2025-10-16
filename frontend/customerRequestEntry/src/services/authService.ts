import type {LoginDtd} from "@/documentTypes/dtds/LoginDtd.ts";
import axios from "axios";
import type {RegisterDtd} from "@/documentTypes/dtds/RegisterDtd.ts";

export async function register(registerData: RegisterDtd) {
  const result = await axios.post(
    '/api/auth/register/customer',
    registerData
  )
  return result.data
}

export async function login(loginData: LoginDtd): Promise<string> {
  const result = await axios.post(
    '/api/auth/login',
    loginData,
    {
      headers: {'Content-Type': 'application/json'},
      withCredentials: true,
    },
  )
  return result.data
}
