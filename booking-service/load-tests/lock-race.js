import http from 'k6/http';
import { Counter } from 'k6/metrics';

const BASE = 'http://localhost:8080';
const headers = { 'Content-Type': 'application/json' };

const VUS = Number(__ENV.VUS) || 100;
const SEAT = Number(__ENV.SEAT) || 4;
const SHOW_ID = Number(__ENV.SHOW_ID) || 3;

export const options = {
  setupTimeout: '5m',
  scenarios: {
    race: { executor: 'per-vu-iterations', vus: VUS, iterations: 1 },
  },
};

export function setup() {
  // runs once before the load
  const tokens = [];

  for(let i=0; i<VUS; i++) {
    const timestamp = Date.now();
    const res = http.post(`${BASE}/auth/register`,
        JSON.stringify({ email: `x${timestamp}${i}@test.com`, password: 'password123', name: 'Demo' }),
        { headers });
    if (res.status !== 201) {
        console.log(res.status);
        console.log(res.body);
        continue;
    }
    const body = res.json();
    tokens.push(body.token);
  }

  console.log(JSON.stringify(tokens));
  return { tokens };
}

export default function (data) {
  const token = data.tokens[__VU - 1];
  const res = http.post(`${BASE}/shows/${SHOW_ID}/seats/lock`,
    JSON.stringify({ seatNumbers: [SEAT] }),
    { headers: { ...headers, Authorization: `Bearer ${token}` } });
  console.log(res.status, res.body);
}