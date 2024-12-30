import os
import psycopg2
from dotenv import load_dotenv
from flask import Flask, request, jsonify
from werkzeug.security import generate_password_hash, check_password_hash

load_dotenv()

database = os.getenv("DATABASE")
user = os.getenv("USER")
password = os.getenv("PASSWORD")
host = os.getenv("HOST")
port = os.getenv("PORT")

app = Flask(__name__)

def get_db_connection():
    conn = psycopg2.connect(
        database=database,
        user=user,
        password=password,
        host=host,
        port=port
    )
    return conn


@app.route('/register', methods=['POST'])
def register():
    data = request.json
    full_name = data['full_name']
    email = data['email']
    password = generate_password_hash(data['password'])

    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            "INSERT INTO users (full_name, email, password) VALUES (%s, %s, %s)",
            (full_name, email, password)
        )
    except Exception as e:
        conn.rollback()
        return jsonify({"error": str(e)}), 400
    else:
        conn.commit()
        return jsonify({"message": "User registered successfully"}), 201
    finally:
        cursor.close()
        conn.close()


@app.route('/login', methods=['POST'])
def login():
    data = request.json
    email = data['email']
    password = data['password']

    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT full_name, email, password FROM users WHERE email = %s", (email,))
    user = cursor.fetchone()

    if user is None:
        return jsonify({"error": "Invalid email"}), 400

    stored_full_name, stored_email, stored_password = user

    if not check_password_hash(stored_password, password):
        return jsonify({"error": "Invalid password"}), 400

    return jsonify({
        "message": "Login successful",
        "user": {
            "full_name": stored_full_name,
            "email": stored_email
        }
    }), 200


if __name__ == '__main__':
    app.run()
