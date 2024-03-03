import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

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
	static Rabbit[] rabbits;
	static int[] dr = {-1, 1, 0, 0};
	static int[] dc = {0, 0, -1, 1};

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Q = Integer.parseInt(br.readLine());
		while (command != 400) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			command = Integer.parseInt(st.nextToken());
			if (command == 100) {
				N = Integer.parseInt(st.nextToken());
				M = Integer.parseInt(st.nextToken());
				P = Integer.parseInt(st.nextToken());
				rabbits = new Rabbit[P];
				int idx = 0;
				while (st.hasMoreTokens()) {
					int p = Integer.parseInt(st.nextToken());
					int d = Integer.parseInt(st.nextToken());
					rabbits[idx++] = new Rabbit(0, 1, 1, p, d, 0, false);
				}
				continue;
			}
			if (command == 200) {
				K = Integer.parseInt(st.nextToken());
				S = Integer.parseInt(st.nextToken());
				while (K-- > 0) {
					Arrays.sort(rabbits);
					Rabbit curr = rabbits[0];
					int[][] info = new int[4][3];
					for (int dir = 0; dir < 4; dir++) {
						int nr = curr.r + curr.dist * dr[dir];
						int nc = curr.c + curr.dist * dc[dir];
						nr = (nr + (N * 2 - 2)) % (N * 2 - 2);
						nc = (nc + (M * 2 - 2)) % (M * 2 - 2);
						if (nr > N) nr = N * 2 - 2 - nr;
						if (nc > M) nc = M * 2 - 2 - nc;
						info[dir][0] = nr + nc;
						info[dir][1] = nr;
						info[dir][2] = nc;
					}
					Arrays.sort(info, (o1, o2) -> {
						if (o1[0] != o2[0]) {
							return Integer.compare(o2[0], o1[0]);
						} else if (o1[1] != o2[1]) {
							return Integer.compare(o2[1], o1[1]);
						} else {
							return Integer.compare(o2[2], o1[2]);
						}
					});
					curr.jump++;
					curr.r = info[0][1];
					curr.c = info[0][2];
					if (!curr.isSelected) curr.isSelected = true;
					for (int idx = 1; idx < P; idx++) {
						rabbits[idx].score += info[0][0];
					}
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
			if (command == 300) {
				int pid_t = Integer.parseInt(st.nextToken());
				L = Integer.parseInt(st.nextToken());
				for (int idx = 0; idx < P; idx++) {
					if (rabbits[idx].pid == pid_t) {
						rabbits[idx].dist *= L;
					}
				}
			}
		}
		int max = Integer.MIN_VALUE;
		for (int idx = 0; idx < P; idx++) {
			max = Math.max(max, rabbits[idx].score);
		}
		System.out.println(max);
	}
}