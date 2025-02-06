<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Device List</title>
    <link rel="stylesheet" href="/css/pico.min.css">
    <script src="/htmx.min.js"></script>
<style>
        body {
            margin: 0;
            display: flex;
            flex-direction: column;
            height: 100vh;
        }

        .header {
            width: 100%;
            text-align: center;
            padding: 1rem;
            box-shadow: 2px 0 5px rgba(0, 0, 0, 0.2);
            z-index: 2;
        }

        .content {
            display: flex;
            flex: 1;
        }

        .menu {
            padding: 2rem;
            /*box-shadow: 2px 0 5px rgba(0, 0, 0, 0.0);*/
            background-color: #FFFFFF;
        }

        .menu ul {
            list-style-type: none;
            padding: 0;
        }

        .menu ul li {
            margin: 10px 0;
        }

        .menu ul li a {
            text-decoration: none;
            color: #333;
            padding: 0.5rem;
            display: block;
            border-radius: 4px;
        }

        .menu ul li a:hover {
            background-color: #007BFF;
            color: white;
        }

        .main {
            flex: 1;
            padding: 1rem;
            background-color: #FBFBFB;
        }
    </style>

</head>