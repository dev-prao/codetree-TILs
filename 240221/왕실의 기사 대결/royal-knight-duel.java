import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {

	static int L, N, Q;
	static int sum;
	static int[][][] board;
	static int[] dr = {-1, 0, 1, 0};
	static int[] dc = {0, 1, 0, -1};
	static Knight[] knights;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());

		board = new int[L][L][N + 1];
		knights = new Knight[N + 1];
		for (int r = 0; r < L; r++) {
			st = new StringTokenizer(br.readLine());
			for (int c = 0; c < L; c++) {
				board[r][c][0] = Integer.parseInt(st.nextToken());
			}
		}

		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			int row = Integer.parseInt(st.nextToken()) - 1;
			int col = Integer.parseInt(st.nextToken()) - 1;
			int height = Integer.parseInt(st.nextToken());
			int width = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			knights[i] = new Knight(row, col, height, width, k, 0);

			for (int r = row; r < row + height; r++) {
				for (int c = col; c < col + width; c++) {
					board[r][c][i] = i;
				}
			}
		}

		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int num = Integer.parseInt(st.nextToken());
			int dir = Integer.parseInt(st.nextToken());
			move(num, dir);
		}

		for (int i = 1; i <= N; i++) {
			int count = 0;
			for (int j = 0; j < N; j++) {
				for (int k = 0; k < N; k++) {
					if (board[j][k][i] > 0) {
						count++;
					}
				}
			}
			if (knights[i].w * knights[i].h == count) {
				sum += knights[i].dmg;
			}
		}
		System.out.println(sum);
	}

	static void move(int index, int dir) {
		Knight curr = knights[index];
		for (int r = 0; r < L; r++) {
			for (int c = 0; c < L; c++) {
				if (board[r][c][index] == index) {
					int nr = r + dr[dir];
					int nc = c + dc[dir];
					if (nr < 0 || nc < 0 || nr + curr.h >= L || nc + curr.w >= L) {
						continue;
					}
					board[r][c][index] = 0;
					board[nr][nc][index] = index;

					if (board[nr][nc][0] == 1) {
						curr.k--;
						curr.dmg++;
					}

					for (int i = 1; i <= N; i++) {
						if (i == index)
							continue;
						if (board[nr][nc][i] != 0) {
							move(i, dir);
						}
					}
				}
			}
		}
	}

	static class Knight {
		int r;
		int c;
		int h;
		int w;
		int k;
		int dmg;

		public Knight(int r, int c, int h, int w, int k, int dmg) {
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.k = k;
			this.dmg = dmg;
		}

	}
}