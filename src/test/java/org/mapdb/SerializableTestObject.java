package org.mapdb;

import java.io.Serializable;
import java.util.Date;

public class SerializableTestObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private int i, j, k;
    private Date a, b, c;
	private double x, y, z;
	private String l, m, n;

	public SerializableTestObject() {
		i = (int) (Math.random() * Integer.MAX_VALUE);
		j = (int) (Math.random() * Integer.MAX_VALUE);
		k = (int) (Math.random() * Integer.MAX_VALUE);
		x = Math.random();
		y = Math.random();
		z = Math.random();
		l = String.valueOf(Math.random());
		m = String.valueOf(Math.random());
		n = String.valueOf(Math.random());
		a = new Date();
		b = new Date();
		c = new Date();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (a == null ? 0 : a.hashCode());
		result = prime * result + (b == null ? 0 : b.hashCode());
		result = prime * result + (c == null ? 0 : c.hashCode());
		result = prime * result + i;
		result = prime * result + j;
		result = prime * result + k;
		result = prime * result + (l == null ? 0 : l.hashCode());
		result = prime * result + (m == null ? 0 : m.hashCode());
		result = prime * result + (n == null ? 0 : n.hashCode());
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ temp >>> 32);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SerializableTestObject other = (SerializableTestObject) obj;
		if (a == null) {
			if (other.a != null) {
				return false;
			}
		} else if (!a.equals(other.a)) {
			return false;
		}
		if (b == null) {
			if (other.b != null) {
				return false;
			}
		} else if (!b.equals(other.b)) {
			return false;
		}
		if (c == null) {
			if (other.c != null) {
				return false;
			}
		} else if (!c.equals(other.c)) {
			return false;
		}
		if (i != other.i) {
			return false;
		}
		if (j != other.j) {
			return false;
		}
		if (k != other.k) {
			return false;
		}
		if (l == null) {
			if (other.l != null) {
				return false;
			}
		} else if (!l.equals(other.l)) {
			return false;
		}
		if (m == null) {
			if (other.m != null) {
				return false;
			}
		} else if (!m.equals(other.m)) {
			return false;
		}
		if (n == null) {
			if (other.n != null) {
				return false;
			}
		} else if (!n.equals(other.n)) {
			return false;
		}
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z)) {
			return false;
		}
		return true;
	}

}
