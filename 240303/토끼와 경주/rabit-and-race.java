import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * 1. 경주 시작 준비(100)
 * P마리의 토끼가 N x M 크기의 격자 위에서 경주 진행 준비
 * i번 토끼의 고유번호는 pid_i, 이동해야 하는 거리 d_i
 *
 * 2. 경주 진행(200) - 200 K(라운드 횟수) S(진행이 끝나고 더할 점수)
 * 가장 우선순위가 높은 토끼를 뽑아 멀리 보내주는 것을 K번 반복
 * 2-1. 우선순위
 *   1) 현재까지의 총 점프 횟수가 적은 토끼
 *   2) 현재 서있는 행 번호(r) + 열 번호(c)가 작은 토끼
 *   3) 행 번호(r)가 작은 토끼
 *   4) 열 번호(c)가 작은 토끼
 *   5) 고유번호(pid)가 작은 토끼
 *
 * 우선순위가 결정되면 상하좌우 네 방향으로 각각 d_i만큼 이동했을 때의 위치를 구함
 * 이때 이동하는 도중 그 다음 칸이 격자를 벗어나게 된다면 방향을 반대로 바꿔 한 칸 이동
 *
 * 이렇게 구해진 4개의 위치 중
 * (행 번호 + 열 번호가 큰 칸, 행 번호가 큰 칸, 열 번호가 큰 칸) 순으로 우선순위
 * 우선순위가 가장 높은 칸을 골라 그 위치로 해당 토끼를 이동
 *
 * 이 칸의 위치를 (r_i, c_i)라 했을 때,
 * i번 토끼를 제외한 나머지 P - 1마리의 토끼들은 전부 r_i + c_i만큼의 점수를 동시에 얻게됨
 * K번의 턴 동안 가장 우선순위가 높은 토끼를 뽑아 멀리 보내주는 것 반복
 * 동일 토끼 여러번 선택 가능
 *
 * K번의 턴이 모두 진행된 직후
 * 1) 현재 서있는 행 번호 + 열 번호가 큰 토끼
 * 2) 행 번호가 큰 토끼
 * 3) 열 번호가 큰 토끼
 * 4) 고유번호가 큰 토끼
 * 순으로 우선순위를 두었을 때 가장 우선순위가 높은 토끼를 골라 점수 S를 더함
 * [K번의 턴 동안 한번이라도 뽑혔던 적이 있던 토끼 중 가장 우선순위가 높은 토끼를 골라야함]
 *
 * 3. 이동거리 변경(300) - 300 pid_t(고유번호) L(거리에 곱할 정수)
 * 고유번호가 pid_t인 토끼의 이동거리를 L배 해줌
 * 토끼의 이동거리가 10억을 넘어가는 일 발생 X
 *
 * 4. 최고의 토끼 선정(400) - 맨 끝에 단 한번만
 * 각 토끼가 모든 경주를 진행하며 얻은 점수 중 가장 높은 점수 출력
 */
public class Main {

	static class Rabbit implements Comparable<Rabbit> {
		int jump, r, c, pid, dist, score;
		boolean isSelected;

		public Rabbit(final int jump, final int r, final int c, final int pid, final int dist,
			final int score, final boolean isSelected) {
			this.jump = jump;
			this.r = r;
			this.c = c;
			this.pid = pid;
			this.dist = dist;
			this.score = score;
			this.isSelected = isSelected;
		}

		@Override
		public String toString() {
			return "Rabbit{" + "jump=" + jump + ", r=" + r + ", c=" + c + ", pid=" + pid + ", dist="
				+ dist + ", score=" + score + ", isSelected=" + isSelected + "}\n\t\t";
		}

		public int compareTo(final Rabbit o) {
			if (this.jump != o.jump) {
				return Integer.compare(this.jump, o.jump);
			} else {
				int thisSum = this.r + this.c;
				int oSum = o.r + o.c;

				if (thisSum != oSum) {
					return Integer.compare(thisSum, oSum);
				} else if (this.r != o.r) {
					return Integer.compare(this.r, o.r);
				} else if (this.c != o.c) {
					return Integer.compare(this.c, o.c);
				} else {
					return Integer.compare(this.pid, o.pid);
				}
			}
		}
	}

	static int Q, command, N, M, P, K, S, L;
	//Q(명령의 수), N(행), M(열), P(토끼의 수), K(턴의 수), S(게임별 점수), L(거리에 곱할 수)
	static int[][][] map; //토끼의 위치
	static Rabbit[] rabbits;
	static int[] dr = {-1, 1, 0, 0};
	static int[] dc = {0, 0, -1, 1};

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Q = Integer.parseInt(br.readLine());
		while (command != 400) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			command = Integer.parseInt(st.nextToken());
			//1.경주준비
			if (command == 100) {
				N = Integer.parseInt(st.nextToken());
				M = Integer.parseInt(st.nextToken());
				P = Integer.parseInt(st.nextToken());
				map = new int[N + 1][M + 1][P];
				rabbits = new Rabbit[P];
				int idx = 0;
				//고유번호와 거리 저장
				while (st.hasMoreTokens()) {
					int p = Integer.parseInt(st.nextToken());
					int d = Integer.parseInt(st.nextToken());
					rabbits[idx++] = new Rabbit(0, 1, 1, p, d, 0, false);
				}
				continue;
			}
			//2.경주진행
			if (command == 200) {
				K = Integer.parseInt(st.nextToken()); //라운드 횟수
				S = Integer.parseInt(st.nextToken()); //더할 점수
				while (K-- > 0) {
					// System.out.println("========================================");
					// System.out.println(K + "번째 라운드 시작합니다 !");
					// System.out.println("========================================");
					//게임진행
					Arrays.sort(rabbits);
					// System.out.println("이동 전 : " + Arrays.toString(rabbits));
					Rabbit curr = rabbits[0];
					int[][] info = new int[4][3];
					for (int dir = 0; dir < 4; dir++) {
						int nr = curr.r + curr.dist * dr[dir];
						int nc = curr.c + curr.dist * dc[dir];
						nr = (nr + (N * 2 - 2)) % (N * 2 - 2);
						nc = (nc + (M * 2 - 2)) % (M * 2 - 2);
						if (nr > N) nr = N * 2 - 2 - nr;
						if (nc > M) nc = N * 2 - 2 - nc;
						info[dir][0] = nr + nc;
						info[dir][1] = nr;
						info[dir][2] = nc;
					}
					//nr + nc , r, c 큰 순으로 정렬
					Arrays.sort(info, (o1, o2) -> {
						if (o1[0] != o2[0]) {
							return Integer.compare(o2[0], o1[0]);
						} else if (o1[1] != o2[1]) {
							return Integer.compare(o2[1], o1[1]);
						} else {
							return Integer.compare(o2[2], o1[2]);
						}
					});

					// System.out.println("저장된 nr, nc 정보");
					// for (int i = 0; i < 4; i++) {
					// 	for (int j = 0; j < 3; j++) {
					// 		System.out.print(info[i][j] + " / ");
					// 	}
					// 	System.out.println();
					// }

					curr.jump++;
					curr.r = info[0][1];
					curr.c = info[0][2];
					if (!curr.isSelected) curr.isSelected = true;
					//움직인 토끼를 제외한 토끼에 nr + nc만큼 더한다.
					for (int idx = 1; idx < P; idx++) {
						rabbits[idx].score += info[0][0];
					}
					// System.out.println("이동 후 : " + Arrays.toString(rabbits));
				}

				Arrays.sort(rabbits, (o1, o2) -> {
					if (o1.r + o1.c == o2.r + o2.c) {
						if (o1.r != o2.r) {
							if (o1.c != o2.c) {
								return Integer.compare(o2.pid, o1.pid);
							} else {
								return Integer.compare(o2.c, o1.c);
							}
						} else {
							return Integer.compare(o2.r, o1.r);
						}
					} else {
						return Integer.compare(o2.r + o2.c, o1.r + o1.c);
					}
				});

				for (int idx = 0; idx < P; idx++) {
					if (rabbits[idx].isSelected) {
						rabbits[idx].score += S;
						break;
					}
				}
				continue;
			}
			//3.이동거리 변경
			if (command == 300) {
				int pid_t = Integer.parseInt(st.nextToken());
				L = Integer.parseInt(st.nextToken()); //곱할 수
				for (int idx = 0; idx < P; idx++) {
					if (rabbits[idx].pid == pid_t) {
						rabbits[idx].dist *= L;
					}
				}
			}
		}
		//최대 점수 출력
		int max = Integer.MIN_VALUE;
		for (int idx = 0; idx < P; idx++) {
			max = Math.max(max, rabbits[idx].score);
		}
		System.out.println(max);
	}
}